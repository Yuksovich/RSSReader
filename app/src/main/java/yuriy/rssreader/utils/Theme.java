package yuriy.rssreader.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import yuriy.rssreader.R;

public final class Theme {

    private static final String FOCUS_THEME = "focus";
    private static final String ORANGE_THEME = "orange";
    private static final String DARK_THEME = "dark";
    private static final String LIGHT_THEME = "light";
    private static final String COLOR_THEME = "key_theme_preferences_screen";

    private static int id = 0;
    private static int textColor = 0;
    private static int textColorSeen = 0;
    private static String htmlStyleCssMiddle = null;

    private Theme() {
        throw new UnsupportedOperationException();
    }

    public static void setTheme(final String themeName, final Context context) {
        switch (themeName) {
            case (FOCUS_THEME):
                id = R.style.MainTheme;
                textColor = context.getResources().getColor(R.color.focus_font_color_list_entry_not_seen);
                textColorSeen = context.getResources().getColor(R.color.focus_font_color_list_entry_was_seen);
                htmlStyleCssMiddle = context.getString(R.string.focus_css_middle_web_view);
                break;
            case (ORANGE_THEME):
                id = R.style.Orange_Blue;
                textColor = context.getResources().getColor(R.color.orange_font_color_list_entry_not_seen);
                textColorSeen = context.getResources().getColor(R.color.orange_font_color_list_entry_was_seen);
                htmlStyleCssMiddle = context.getString(R.string.orange_css_middle_web_view);
                break;
            case (DARK_THEME):
                id = R.style.Dark;
                textColor = context.getResources().getColor(R.color.dark_font_color_list_entry_not_seen);
                textColorSeen = context.getResources().getColor(R.color.dark_font_color_list_entry_was_seen);
                htmlStyleCssMiddle = context.getString(R.string.dark_css_middle_web_view);
                break;
            case (LIGHT_THEME):
                id = R.style.Light;
                textColor = context.getResources().getColor(R.color.light_font_color_list_entry_not_seen);
                textColorSeen = context.getResources().getColor(R.color.light_font_color_list_entry_was_seen);
                htmlStyleCssMiddle = context.getString(R.string.light_css_middle_web_view);
                break;
            default:

                break;
        }
    }

    public static int getId() {
        return id;
    }

    public static String getHtmlStyleCssMiddle(final Context context){
        if (htmlStyleCssMiddle==null){
            return context.getString(R.string.focus_css_middle_web_view);
        }
        else {
            return htmlStyleCssMiddle;
        }
    }

    public static int getTextColor(final Context context){
        if(textColor==0){
            return context.getResources().getColor(R.color.focus_font_color_list_entry_not_seen);
        }
        else{
            return textColor;
        }
    }

    public static int getTextColorSeen(final Context context){
        if(textColorSeen==0){
            return context.getResources().getColor(R.color.focus_font_color_list_entry_was_seen);
        }
        else{
            return textColorSeen;
        }
    }

    public static void setThemeFromSharedPreferences(final Context context){
        final SharedPreferences sPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        setTheme(sPrefs.getString(COLOR_THEME, FOCUS_THEME), context);
    }
}
