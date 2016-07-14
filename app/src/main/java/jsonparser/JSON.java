package jsonparser;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/*import com.bitmakers.techmonster.model_class.CityList;
import com.bitmakers.techmonster.model_class.CompanyInfo;
import com.bitmakers.techmonster.model_class.DistList;
import com.bitmakers.techmonster.model_class.JobDetailsList;
import com.bitmakers.techmonster.model_class.JobList;*/

/**
 * Created by MAHBUB_MUKUL on 6/13/2016.
 */
public class JSON {


    /*public ArrayList<JobList> parseHomeNews(String json){

        ArrayList<JobList> temp = new ArrayList<JobList>();

        System.out.println("FFFFFFFFFFFFFFFFFF _JSONNEWS  ..........  "+json);
        try {
            if(json.startsWith("Error"))
                return null;

            JSONObject jObj = new JSONObject(json);
            JSONArray contacts = jObj.getJSONArray("list_job");

            System.out.println("FFFFFFFFFFFFFFFFFF _JSONNEWSL.........  "+contacts.length());

            for(int i = 0; i < contacts.length(); i++){
                JSONObject c = contacts.getJSONObject(i);

                String id="",name="",salary="",summary="",job_cover_image ="",
                        job_city="",job_dist="",expire_time="",company_id="",company_name="", companyTxt="";

                try{id = c.getString("id");}catch (Exception e){}
                try{name = c.getString("name");}catch (Exception e){}
                try{salary = c.getString("salary");}catch (Exception e){}
                try{summary = c.getString("summary");}catch (Exception e){}
                try{job_cover_image = c.getString("job_cover_image");}catch (Exception e){}
                try{job_city = c.getString("job_city");}catch (Exception e){}
                try{job_dist = c.getString("job_dist");}catch (Exception e){}
                try{expire_time = c.getString("expire_time");}catch (Exception e){}

                try{companyTxt = c.getString("company_info");
                    JSONObject comObj = new JSONObject(companyTxt);
                    try{company_id = comObj.getString("id_company");}catch (Exception e){}
                    try{company_name = comObj.getString("com_name");}catch (Exception e){}

                }catch (Exception e){}

                JobList jj = new JobList();
                jj.setId(id);jj.setCompany_id(company_id);jj.setCompany_name(company_name);
                jj.setExpire_time(expire_time);jj.setJob_city(job_city);jj.setJob_country(job_dist);
                jj.setJob_cover_image(job_cover_image);jj.setName(name);jj.setSalary(salary);jj.setSummary(summary);

                temp.add(jj);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  temp;
    }

    public ArrayList<JobDetailsList> parseDetailsNews(String json){
        ArrayList<JobDetailsList> temp = new ArrayList<JobDetailsList>();
        String status="";
        try {
            if(json.startsWith("Error"))
                return null;
            JSONObject c1 = new JSONObject(json);

            String job_info_obj="",summary="",view_num="",job_type="",repost_num="",create_time ="", keywords_arr []={""},expire_time="",job_exp_arr[]={""},salary ="",
                    created_by="",job_cover_image="",applicant_job_status="",job_skill_arr[]={""}, start_time="",job_country ="",name ="",
                    salary_min="",job_city="",id="",detail ="", job_dist="",applicant_company_status ="",

                    company_info_obj ="",com_map_obj ="",places_obj ="",formatted_address="",location_obj="",lat="",lng="",com_linked_in ="", com_who_we_are="",com_address ="",com_size ="",id_company ="",
                    com_phone="",com_website="",com_country="",com_district="",com_facebook ="", com_benefit_arr[]={""},com_thumbnail ="",company_image_arr []={""},
                    com_intro ="",com_city="",com_logo="",com_desc="",com_name ="";
            try{status = c1.getString("message");}catch (Exception e){}


            if(status.trim().equals("Success"))
            {
                try{
                    JSONObject c = c1.getJSONObject("job_info");

                    try{summary = c.getString("summary");}catch (Exception e){}
                    try{view_num = c.getString("view_num");}catch (Exception e){}
                    try{job_type = c.getString("job_type");}catch (Exception e){}
                    try{repost_num = c.getString("repost_num");}catch (Exception e){}
                    try{create_time = c.getString("create_time");}catch (Exception e){}
                    try{
                        String aa = c.getString("keywords");

                        JSONArray jsonArray = new JSONArray(aa);
                        keywords_arr = new String[jsonArray.length()];
                        for(int ju=0;ju<jsonArray.length();ju++)
                        {
                            keywords_arr[ju] =  jsonArray.getString(ju);
                        }
                    }catch (Exception e){}
                    try{expire_time = c.getString("expire_time");}catch (Exception e){}
                    try{
                        JSONArray jsonArray = c.getJSONArray("job_exp");
                        job_exp_arr = new String[jsonArray.length()];
                        for(int ju=0;ju<jsonArray.length();ju++)
                        {
                            job_exp_arr[ju] =  jsonArray.getString(ju);
                        }
                    }catch (Exception e){}

                    try{salary = c.getString("salary");}catch (Exception e){}
                    try{created_by = c.getString("created_by");}catch (Exception e){}
                    try{job_cover_image = c.getString("job_cover_image");}catch (Exception e){}
                    try{applicant_job_status = c.getString("applicant_job_status");}catch (Exception e){}
                    try{
                        JSONArray jsonArray = c.getJSONArray("job_skill");
                        job_skill_arr = new String[jsonArray.length()];
                        for(int ju=0;ju<jsonArray.length();ju++)
                        {
                            job_skill_arr[ju] =  jsonArray.getString(ju);
                        }
                    }catch (Exception e){}
                    try{start_time = c.getString("start_time");}catch (Exception e){}
                    try{job_country = c.getString("job_country");}catch (Exception e){}
                    try{name = c.getString("name");}catch (Exception e){}
                    try{salary_min = c.getString("salary_min");}catch (Exception e){}
                    try{job_city = c.getString("job_city");}catch (Exception e){}
                    try{id = c.getString("id");}catch (Exception e){}
                    try{detail = c.getString("detail");}catch (Exception e){}
                    try{job_dist = c.getString("job_dist");}catch (Exception e){}
                    try{applicant_company_status = c.getString("applicant_company_status");}catch (Exception e){}
                    try{
                        JSONObject cc = c.getJSONObject("company_info");
                        try{
                            com_map_obj = cc.getString("com_map");
                            JSONObject ccm = new JSONObject(com_map_obj);
                            try{
                                places_obj = ccm.getString("places");
                                JSONObject ccmp = new JSONObject(places_obj);
                                try{formatted_address = ccmp.getString("formatted_address");}catch (Exception e){}

                            }catch (Exception e){}
                            try{
                                location_obj = ccm.getString("location");
                                JSONObject ccml = new JSONObject(location_obj);
                                try{lat = ccml.getString("lat");}catch (Exception e){}
                                try{lng = ccml.getString("lng");}catch (Exception e){}

                                System.out.println("WWWWWW >>>"+lat+" "+lng);
                            }catch (Exception e){}

                        }catch (Exception e){}
                        try{com_linked_in = cc.getString("com_linked_in");}catch (Exception e){}
                        try{com_who_we_are = cc.getString("com_who_we_are");}catch (Exception e){}
                        try{com_address = cc.getString("com_address");}catch (Exception e){}
                        try{com_size = cc.getString("com_size");}catch (Exception e){}
                        try{id_company = cc.getString("id_company");}catch (Exception e){}
                        try{com_phone = cc.getString("com_phone");}catch (Exception e){}
                        try{com_website = cc.getString("com_website");}catch (Exception e){}
                        try{com_country = cc.getString("com_country");}catch (Exception e){}
                        try{com_district = cc.getString("com_district");}catch (Exception e){}
                        try{com_facebook = cc.getString("com_facebook");}catch (Exception e){}
                        try{
                            JSONArray jsonArray = cc.getJSONArray("com_benefit");
                            com_benefit_arr = new String[jsonArray.length()];
                            for(int ju=0;ju<jsonArray.length();ju++)
                            {
                                com_benefit_arr[ju] =  jsonArray.getString(ju);
                            }
                        }catch (Exception e){}

                        try{com_thumbnail = cc.getString("com_thumbnail");}catch (Exception e){}
                        try{
                            JSONArray jsonArray = cc.getJSONArray("company_image");
                            company_image_arr = new String[jsonArray.length()];
                            for(int ju=0;ju<jsonArray.length();ju++)
                            {
                                company_image_arr[ju] =  jsonArray.getString(ju);
                            }
                        }catch (Exception e){}
                        try{com_intro = cc.getString("com_intro");}catch (Exception e){}
                        try{com_city = cc.getString("com_city");}catch (Exception e){}
                        try{com_logo = cc.getString("com_logo");}catch (Exception e){}
                        try{com_desc = cc.getString("com_desc");}catch (Exception e){}
                        try{com_name = cc.getString("com_name");}catch (Exception e){}

                    }catch (Exception e){}

                }catch (Exception e){
                    System.out.println("WWWWWW >>>"+e.toString());
                }


                CompanyInfo ci = new CompanyInfo();
                ci.setFormatted_address(formatted_address);ci.setLat(lat);ci.setLng(lng);ci.setCom_address(com_address);ci.setCom_benefit(com_benefit_arr);ci.setCom_city(com_city);ci.setCom_country(com_country);
                ci.setCom_desc(com_desc);ci.setCom_district(com_district);ci.setCom_facebook(com_facebook);ci.setCom_intro(com_intro);ci.setCom_logo(com_logo);ci.setCom_name(com_name);
                ci.setCom_phone(com_phone);ci.setCom_size(com_size);ci.setCom_thumbnail(com_thumbnail);ci.setCom_who_we_are(com_who_we_are);ci.setCom_website(com_website);ci.setCom_linked_in(com_linked_in);
                ci.setId_company(id_company);ci.setCompany_image(company_image_arr);

                JobDetailsList jd = new JobDetailsList();
                jd.setApplicant_company_status(applicant_company_status);jd.setApplicant_job_status(applicant_job_status);jd.setCompany_info(ci);
                jd.setCreate_time(create_time);jd.setCreated_by(created_by);jd.setDetail(detail);jd.setExpire_time(expire_time);jd.setId(id);jd.setJob_city(job_city);
                jd.setJob_country(job_country);jd.setView_num(view_num);jd.setSummary(summary);jd.setStart_time(start_time);jd.setSalary_min(salary_min);jd.setRepost_num(repost_num);
                jd.setName(name);jd.setKeywords(keywords_arr);jd.setJob_type(job_type);jd.setJob_skill(job_skill_arr);jd.setJob_exp(job_exp_arr);jd.setSalary(salary);
                jd.setJob_dist(job_dist);jd.setJob_cover_image(job_cover_image);

                temp.add(jd);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  temp;
    }

    public ArrayList<CityList> parseCity(String json){

        ArrayList<CityList> temp = new ArrayList<CityList>();
        try {
            if(json.startsWith("Error"))
                return null;

            JSONObject jObj = new JSONObject(json);
            JSONArray contacts = jObj.getJSONArray("data");



            for(int i = 0; i < contacts.length(); i++){
                JSONObject c = contacts.getJSONObject(i);

                String id="",name="",country_id="",dist_id="",city_id ="",
                        dist_name="",disttxt="";

                ArrayList<DistList> temp1 = new ArrayList<DistList>();

                try{id = c.getString("id_city");}catch (Exception e){}
                try{name = c.getString("city_name");}catch (Exception e){}
                try{country_id = c.getString("id_country");}catch (Exception e){}
                try{
                    disttxt = c.getString("districts");
                    JSONArray districts = new JSONArray(disttxt);

                    for(int j = 0; j < districts.length(); j++){
                        JSONObject c1 = districts.getJSONObject(j);

                        try{dist_id = c1.getString("id");}catch (Exception e){}
                        try{dist_name = c1.getString("name");}catch (Exception e){}
                        try{city_id = c1.getString("id_city");}catch (Exception e){}
                        DistList jj = new DistList();
                        jj.setId(dist_id);jj.setName(dist_name);jj.setCity_id(city_id);

                        temp1.add(jj);
                    }

                }catch (Exception e){}

                CityList jk = new CityList();
                jk.setId(id);jk.setName(name);jk.setCountry_id(country_id);
                jk.setDistricts(temp1);

                temp.add(jk);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  temp;
    }*/

    public ArrayList<String> parseKey(String json) {

        ArrayList<String> temp = new ArrayList<String>();
        try {
            if (json.startsWith("Error"))
                return null;

            JSONObject jObj = new JSONObject(json);
            JSONArray contacts = jObj.getJSONArray("list_keywords");

            System.out.println("Fuchka :" + contacts.length());
            for (int i = 0; i < contacts.length(); i++) {
                String jk = contacts.getString(i);

                System.out.println("Fuchka :" + jk);

                temp.add(jk);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return temp;
    }

    public String parseSignIn(String json, Context ctx) {

        String status = "";
        try {
            if (json.startsWith("Error"))
                return "Error";

            JSONObject c = new JSONObject(json);

            String id = "", company_id = "", name = "", rid = "", ttl = "",
                    email = "", token = "", avatar = "", array_company = "";
            try {
                status = c.getString("message");
            } catch (Exception e) {
            }

            if (status.equals("Success")) {
                try {
                    id = c.getString("uid");
                } catch (Exception e) {
                }
                try {
                    name = c.getString("name");
                } catch (Exception e) {
                }
                try {
                    company_id = c.getString("company_id");
                } catch (Exception e) {
                }
                try {
                    rid = c.getString("rid");
                } catch (Exception e) {
                }
                try {
                    ttl = c.getString("ttl");
                } catch (Exception e) {
                }
                try {
                    email = c.getString("email");
                } catch (Exception e) {
                }
                try {
                    token = c.getString("token");
                } catch (Exception e) {
                }
                try {
                    String disttxt = c.getString("info");
                    JSONObject c1 = new JSONObject(disttxt);
                    try {
                        avatar = c1.getString("avatar");
                    } catch (Exception e) {
                    }
                    try {
                        array_company = c1.getString("array_company");
                    } catch (Exception e) {
                    }
                } catch (Exception e) {
                }

                SharedPreferences pref = ctx.getSharedPreferences("TechMonster_Login", 0); // 0 - for private mode
                SharedPreferences.Editor editor = pref.edit();

                editor.putString("user_id", id);
                editor.putString("user_name", name);
                editor.putString("user_company_id", company_id);
                editor.putString("user_rid", rid);
                editor.putString("user_ttl", ttl);
                editor.putString("user_email", email);
                editor.putString("user_token", token);
                editor.putString("user_avatar", avatar);
                editor.putString("user_company", array_company);

                editor.commit(); // commit changes

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return status;
    }

    public String parseSignUp(String json, Context ctx) {

        String status = "";
        try {
            if (json.startsWith("Error"))
                return "Error";

            JSONObject c = new JSONObject(json);

            String id = "", company_id = "", name = "", rid = "", ttl = "",
                    email = "", token = "", avatar = "", array_company = "";
            try {
                status = c.getString("message");
            } catch (Exception e) {
            }

            if (status.equals("Success")) {
//                try{id = c.getString("uid");}catch (Exception e){}
//                try{name = c.getString("name");}catch (Exception e){}
//                try{company_id = c.getString("company_id");}catch (Exception e){}
//                try{rid = c.getString("rid");}catch (Exception e){}
//                try{ttl = c.getString("ttl");}catch (Exception e){}
//                try{email = c.getString("email");}catch (Exception e){}
//                try{token = c.getString("token");}catch (Exception e){}
//                try{
//                    String disttxt = c.getString("info");
//                    JSONObject c1 = new JSONObject(disttxt);
//                    try{avatar = c1.getString("avatar");}catch (Exception e){}
//                    try{array_company = c1.getString("array_company");}catch (Exception e){}
//                }catch (Exception e){}
//
//                SharedPreferences pref = ctx.getSharedPreferences("TechMonster_Login", 0); // 0 - for private mode
//                SharedPreferences.Editor editor = pref.edit();
//
//                editor.putString("user_id", id);
//                editor.putString("user_name", name);
//                editor.putString("user_company_id", company_id);
//                editor.putString("user_rid", rid);
//                editor.putString("user_ttl", ttl);
//                editor.putString("user_email", email);
//                editor.putString("user_token", token);
//                editor.putString("user_avatar", avatar);
//                editor.putString("user_company", array_company);
//
//                editor.commit(); // commit changes

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return status;
    }
}
