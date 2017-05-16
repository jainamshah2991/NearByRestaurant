package com.nearbyrestaurant.activities;

import android.os.Bundle;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nearbyrestaurant.R;
import com.nearbyrestaurant.contracts.BusinessDetailContract;
import com.nearbyrestaurant.presenterimpl.BusinessDetailPresenter;
import com.nearbyrestaurant.utils.AppConstant;
import com.squareup.picasso.Picasso;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.Review;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BusinessDetailActivity extends BaseActivity implements BusinessDetailContract.View {

    @BindView(R.id.tv_app_toolbar_title)
    TextView tvAppToolbarTitle;
    @BindView(R.id.iv_business_pic)
    ImageView ivBusinessPic;
    @BindView(R.id.tv_business_name)
    TextView tvBusinessName;
    @BindView(R.id.rb_business_ratings)
    AppCompatRatingBar rbBusinessRatings;
    @BindView(R.id.tv_business_address)
    TextView tvBusinessAddress;
    @BindView(R.id.tb_business_detail_screen)
    Toolbar tbBusinessDetailScreen;
    @BindView(R.id.tv_business_phone)
    TextView tvBusinessPhone;
    @BindView(R.id.tv_business_review_count)
    TextView tvBusinessReviewCount;
    @BindView(R.id.iv_review_user_pic)
    ImageView ivReviewUserPic;
    @BindView(R.id.tv_review_user_name)
    TextView tvReviewUserName;
    @BindView(R.id.rb_review_user_ratings)
    AppCompatRatingBar rbReviewUserRatings;
    @BindView(R.id.tv_review_user_message)
    TextView tvReviewUserMessage;
    @BindView(R.id.ll_business_review)
    LinearLayout llBusinessReview;

    private BusinessDetailPresenter businessDetailPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_detail);
        ButterKnife.bind(this);

        init();
    }

    private void init() {

        businessDetailPresenter = new BusinessDetailPresenter(this, this);

        setSupportActionBar(tbBusinessDetailScreen);
        tbBusinessDetailScreen.setNavigationIcon(R.drawable.backarrow_white);

        tvAppToolbarTitle.setText(getResources().getString(R.string.screen_title_business_detail));

        tbBusinessDetailScreen.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (getIntent() != null && getIntent().hasExtra(AppConstant.INTENT_KEY_BUSINESS_ID)) {
            businessDetailPresenter.getBusinessDetailFromServer(getIntent().getStringExtra(AppConstant.INTENT_KEY_BUSINESS_ID));
        }

    }

    @Override
    public void displayBusinessDetailData(Business business) {

        /*  Set business name   */
        if (!TextUtils.isEmpty(business.name()))
            tvBusinessName.setText(business.name());
        else
            tvBusinessName.setText(getResources().getString(R.string.not_available_label));

        /*  Set ratings    */
        if (business.rating() != null)
            rbBusinessRatings.setRating((float) business.rating().doubleValue());
        else
            rbBusinessRatings.setRating(0);

        /*  Set Business Address    */
        if (business.location() != null && business.location().displayAddress() != null)
            tvBusinessAddress.setText(TextUtils.join(", ", business.location().displayAddress()));
        else
            tvBusinessAddress.setText(getResources().getString(R.string.not_available_label));

        /*  Set image   */
        if (!TextUtils.isEmpty(business.imageUrl()))
            Picasso.with(this).load(business.imageUrl()).placeholder(R.drawable.ic_place_holder)
                    .into(ivBusinessPic);
        else
            ivBusinessPic.setImageResource(R.drawable.ic_place_holder);

        /*  Set Phone number    */
        if (!TextUtils.isEmpty(business.displayPhone()))
            tvBusinessPhone.setText(business.displayPhone());
        else
            tvBusinessPhone.setText(getResources().getString(R.string.not_available_label));

        /*  Set reviews */
        if (business.reviewCount() != null && business.reviewCount().doubleValue() > 0) {
            llBusinessReview.setVisibility(View.VISIBLE);

            /*  set review count    */
            tvBusinessReviewCount.setText("(" + (int)business.reviewCount().doubleValue() + ")");

            /*  Set user information   */
            if (business.reviews() != null && !business.reviews().isEmpty()) {
                Review review = business.reviews().get(0);

                if (review.user() != null) {
                    /*  Set user pic    */

                    if (!TextUtils.isEmpty(review.user().imageUrl()))
                        Picasso.with(this).load(review.user().imageUrl()).placeholder(R.drawable.ic_place_holder)
                                .into(ivReviewUserPic);
                    else
                        ivReviewUserPic.setImageResource(R.drawable.ic_place_holder);

                    /*  Set user name   */
                    if (!TextUtils.isEmpty(review.user().name()))
                        tvReviewUserName.setText(review.user().name());
                    else
                        tvReviewUserName.setText(getResources().getString(R.string.not_available_label));
                }

                /*  Set review ratings    */
                if (review.rating() != null)
                    rbReviewUserRatings.setRating((float) review.rating().doubleValue());
                else
                    rbReviewUserRatings.setRating(0);

                /*  Set review message  */
                if (!TextUtils.isEmpty(review.excerpt()))
                    tvReviewUserMessage.setText(review.excerpt());
                else
                    tvReviewUserMessage.setText(getResources().getString(R.string.not_available_label));
            }

        } else {
            llBusinessReview.setVisibility(View.GONE);
        }
    }
}
