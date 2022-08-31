package com.zakux.live.activity.purchase;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.razorpay.Checkout;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.model.StripeIntent;
import com.stripe.android.view.CardInputWidget;
import com.stripe.param.PaymentIntentCreateParams;
import com.zakux.live.R;
import com.zakux.live.SessionManager;
import com.zakux.live.adaptor.VipPlanAdapter;
import com.zakux.live.databinding.BottomSheetCardBinding;
import com.zakux.live.databinding.BottomSheetPaymentBinding;
import com.zakux.live.databinding.FragmentVipPlanListBinding;
import com.zakux.live.models.BecomeVipMemberRoot;
import com.zakux.live.models.User;
import com.zakux.live.models.VipPlanRoot;
import com.zakux.live.retrofit.Const;
import com.zakux.live.retrofit.RetrofitBuilder;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VipPlanListFragment extends Fragment {


    private static final String STR_GP = "google pay";
    private static final String STR_STRIPE = "stripe";
    private static final String STR_VIP = "You Are Vip Member";
    FragmentVipPlanListBinding binding;
    Checkout checkout = new Checkout();
    BottomSheetCardBinding bottomSheetCardBinding;
    private String paymentGateway;
    private SessionManager sessionManager;
    private String userId;
    private User user;
    String apiKey;
    private Stripe stripe;
    private String planId;

    private BottomSheetDialog bottomSheetDialog;

    public VipPlanListFragment() {
        // Required empty public constructor
    }


    public VipPlanListFragment(String paymentGateway) {

        this.paymentGateway = paymentGateway;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_vip_plan_list, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sessionManager = new SessionManager(getActivity());
        userId = sessionManager.getUser().getId();
        user = sessionManager.getUser();
        Log.d("TAG", "onActivityCreated: " + paymentGateway);

        getData();

        Checkout.preload(getActivity());
        checkout.setKeyID(sessionManager.getSetting().getRazorPayId());

        apiKey = Const.STRIPE_SECRET_KEY;

        stripe = new Stripe(
                getActivity().getApplicationContext(),
                Objects.requireNonNull(Const.STRIPE_PUBLISHABLE_KEY)
        );


    }

    private void getData() {
        Call<VipPlanRoot> call = RetrofitBuilder.create().getVipPlans(Const.DEVKEY);
        call.enqueue(new Callback<VipPlanRoot>() {
            private void onButClick(VipPlanRoot.DataItem dataItem) {
                openBottomSheet(dataItem);
            }

            @Override
            public void onResponse(Call<VipPlanRoot> call, Response<VipPlanRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && !response.body().getData().isEmpty()) {
                        VipPlanAdapter moreCoinAdapter = new VipPlanAdapter(response.body().getData(), this::onButClick);
                        binding.rvMoreCoins.setAdapter(moreCoinAdapter);

                    }
                }
            }

            @Override
            public void onFailure(Call<VipPlanRoot> call, Throwable t) {
//ll
            }
        });
    }

    private void openBottomSheet(VipPlanRoot.DataItem dataItem) {
        if (getActivity() == null) return;

        bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.CustomBottomSheetDialogTheme);
        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = (FrameLayout) d.findViewById(R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheet)
                    .setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        BottomSheetPaymentBinding bottomSheetPaymentBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.bottom_sheet_payment, null, false);
        bottomSheetDialog.setContentView(bottomSheetPaymentBinding.getRoot());
        bottomSheetDialog.show();
        bottomSheetPaymentBinding.btnclose.setOnClickListener(v -> bottomSheetDialog.dismiss());
        List<String> pg = ((CoinPlanActivity) getActivity()).getPaymentGateways();
        if (pg.contains(STR_GP)) {
            bottomSheetPaymentBinding.lytgooglepay.setVisibility(View.VISIBLE);

            bottomSheetPaymentBinding.lytgooglepay.setOnClickListener(v -> {
                paymentGateway = STR_GP;
                buyItem(dataItem);
            });
        } else {
            bottomSheetPaymentBinding.lytgooglepay.setVisibility(View.GONE);
        }
        if (pg.contains("razor pay")) {

            bottomSheetPaymentBinding.lytrazorpay.setVisibility(View.VISIBLE);
            bottomSheetPaymentBinding.lytrazorpay.setOnClickListener(v -> {
                paymentGateway = "razor pay";
                buyItem(dataItem);
            });
        } else {
            bottomSheetPaymentBinding.lytrazorpay.setVisibility(View.GONE);
        }
        if (pg.contains(STR_STRIPE)) {
            bottomSheetPaymentBinding.lytstripe.setVisibility(View.VISIBLE);
            bottomSheetPaymentBinding.lytstripe.setOnClickListener(v -> {
                paymentGateway = STR_STRIPE;
                buyItem(dataItem);
            });
        } else {
            bottomSheetPaymentBinding.lytstripe.setVisibility(View.GONE);
        }


    }

    private void buyItem(VipPlanRoot.DataItem dataItem) {
        planId = dataItem.getId();
        ((CoinPlanActivity) getActivity()).setSelectedPlanId(dataItem.getId(), true);
        if (paymentGateway.equals(STR_GP)) {

            planId = dataItem.getId();
            ((CoinPlanActivity) getActivity()).setSelectedPlanId(planId, true);

            ((CoinPlanActivity) getActivity()).makeGooglePurchase(dataItem.getProductid());

        } else if (paymentGateway.equals(STR_STRIPE)) {

            bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.CustomBottomSheetDialogTheme);
            bottomSheetCardBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.bottom_sheet_card, null, false);
            bottomSheetDialog.setContentView(bottomSheetCardBinding.getRoot());
            bottomSheetCardBinding.cardInputWidget.setPostalCodeEnabled(false);
            bottomSheetCardBinding.cardInputWidget.setPostalCodeRequired(false);

            bottomSheetCardBinding.btnclose.setOnClickListener(v -> bottomSheetDialog.dismiss());
            bottomSheetCardBinding.tvamount.setText(String.valueOf(dataItem.getPrice()));
            bottomSheetCardBinding.tvcoin.setText(String.valueOf(dataItem.getTime()));
            bottomSheetCardBinding.btnPay.setOnClickListener(v -> {
                PaymentMethodCreateParams params = bottomSheetCardBinding.cardInputWidget.getPaymentMethodCreateParams();
                if (params != null) {
                    new MyTask().execute();
                    Log.d("TAG", "confirmPayment: ");
                    bottomSheetDialog.dismiss();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.entercarddetails), Toast.LENGTH_SHORT).show();
                }
            });
            bottomSheetDialog.show();

        } else {
            if (checkout != null) {
                checkout.setImage(R.drawable.coin);
                try {
                    Activity activity = getActivity();
                    JSONObject options = new JSONObject();

                    options.put("name", sessionManager.getUser().getName());
                    options.put("description", "user id: " + sessionManager.getUser().getId());
                    options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
                    //   options.put("order_id", "order_DBJOWzybf0sJbb");//from response of step 3.
                    //options.put("theme.color", "#3399cc");
                    options.put("currency", "INR");


                    Log.d("TAG", "razorpay : rate2= " + dataItem.getPrice());
                    options.put("amount", 100 * dataItem.getPrice());


                    options.put("prefill.email", sessionManager.getUser().getIdentity());
                    options.put("prefill.contact", "0000000000");
                    checkout.open(activity, options);


                } catch (Exception e) {
                    Log.e("TAG", "Error in submitting payment details", e);
                }
            }

        }


    }


    private void callPurchaseDoneApi() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", userId);
        jsonObject.addProperty("plan_id", planId);
        Call<BecomeVipMemberRoot> call = RetrofitBuilder.create().becomeVip(Const.DEVKEY, jsonObject);
        call.enqueue(new Callback<BecomeVipMemberRoot>() {
            @Override
            public void onResponse(Call<BecomeVipMemberRoot> call, Response<BecomeVipMemberRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && response.body().getUser() != null) {
                        Toast.makeText(getActivity(), STR_VIP, Toast.LENGTH_SHORT).show();
                        sessionManager.saveUser(response.body().getUser());

                    } else {
                        Toast.makeText(getActivity(), STR_VIP, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), STR_VIP, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BecomeVipMemberRoot> call, Throwable t) {
                Toast.makeText(getActivity(), "Something Went Wrong..", Toast.LENGTH_SHORT).show();
            }
        });

    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        Log.d("TAG", "onActivityResult: ");
        stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(this));
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void displayAlert(@NonNull String title,
                              @Nullable String message, boolean b) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message);
        builder.setPositiveButton("Ok", (dialog, which) -> {
            if (b) {
                Toast.makeText(getActivity(), "Purchase Done", Toast.LENGTH_SHORT).show();
                callPurchaseDoneApi();
            } else {
                builder.create().dismiss();
            }
        });
        builder.create().show();
    }

    private static final class PaymentResultCallback
            implements ApiResultCallback<PaymentIntentResult> {


        private final WeakReference<Fragment> activityRef;

        public PaymentResultCallback(VipPlanListFragment planListFragment) {
            activityRef = new WeakReference<>(planListFragment);
            Log.d("TAG", "PaymentResultCallback: ");
        }

        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            VipPlanListFragment activity = (VipPlanListFragment) activityRef.get();
            if (activity == null) {
                return;
            }
            PaymentIntent paymentIntent = result.getIntent();
            StripeIntent.Status status = paymentIntent.getStatus();
            if (status == StripeIntent.Status.Succeeded) {

                // Payment completed successfully
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                Log.d("TAG", "onSuccess: payment== " + gson.toString());
                long amount = paymentIntent.getAmount() / 100;
                String message = "Amount: " + amount + "$" + "\n Status: " + paymentIntent.getStatus().toString();

                activity.displayAlert(
                        "Payment completed",
                        message, true
                );
            } else if (status == StripeIntent.Status.RequiresPaymentMethod) {
                // Payment failed – allow retrying using a different payment method
                activity.displayAlert(
                        "Payment failed",
                        Objects.requireNonNull(paymentIntent.getLastPaymentError()).getMessage(), false
                );
            }
        }

        @Override
        public void onError(@NonNull Exception e) {
            VipPlanListFragment activity = (VipPlanListFragment) activityRef.get();
            if (activity == null) {
                return;
            }
            // Payment request failed – allow retrying using the same payment method
            Log.d("TAG", "onSuccess: error== " + e.toString());
            activity.displayAlert("Error", e.getMessage(), false);
        }
    }

    private class MyTask extends AsyncTask<String, String, String> {


        private String paymentIntentClientSecret;

        @Override
        protected String doInBackground(String... strings) {

            com.stripe.Stripe.apiKey = Const.STRIPE_SECRET_KEY;

            PaymentIntentCreateParams params1 =
                    PaymentIntentCreateParams.builder()
                            .setAmount((long) 100)
                            .setDescription(user.getId())
                            .setReceiptEmail(user.getIdentity())
                            //   .putExtraParam("email",sessionManager.getUser().getData().getEmail())
                            .setShipping(
                                    PaymentIntentCreateParams.Shipping.builder()
                                            .setName(user.getName())
                                            .setPhone("0000000000")
                                            .setAddress(
                                                    PaymentIntentCreateParams.Shipping.Address.builder()
                                                            .setLine1("abc")
                                                            .setPostalCode("91761")
                                                            .setLine2("def")
                                                            .setCity("city")
                                                            .setState("sar")
                                                            .setCountry("US")
                                                            .build())
                                            .build())
                            .setCurrency("USD")
                            .addPaymentMethodType("card")
                            .build();

            com.stripe.model.PaymentIntent paymentIntent = null;

            try {
                paymentIntent = com.stripe.model.PaymentIntent.create(params1);
            } catch (com.stripe.exception.StripeException e) {
                e.printStackTrace();
                Log.d("TAG", "startCheckout: errr 64 " + e);
            }


            paymentIntentClientSecret = paymentIntent != null ? paymentIntent.getClientSecret() : null;
            Log.d("TAG", "doInBackground:0 " + paymentIntentClientSecret);

            Log.d("TAG", "doInBackground:1 " + paymentIntentClientSecret);
            return paymentIntentClientSecret;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            CardInputWidget cardInputWidget = bottomSheetCardBinding.cardInputWidget;
            cardInputWidget.setPostalCodeRequired(false);
            cardInputWidget.setPostalCodeEnabled(false);
            PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();

            if (params != null && paymentIntentClientSecret != null) {
                Log.d("TAG", "confirmPayment: " + params.toString());
                ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                        .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);
                stripe.confirmPayment(VipPlanListFragment.this, confirmParams);

                Log.d("TAG", "onResponse: cps == " + confirmParams.getClientSecret());
            }
        }

    }

}