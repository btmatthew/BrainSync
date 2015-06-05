package com.note.anonymous.brainsync;

import android.util.Log;

import java.io.File;
import java.util.Comparator;

/**
 * Created by Matthew Bulat on 03/06/2015.
 */
public class Sorting {
    public static class CustomComparatorByTitle implements Comparator<Filenames> {

        @Override
        public int compare(Filenames lhs, Filenames rhs) {

            return (lhs).getFilename().toLowerCase().compareTo(rhs.getFilename().toLowerCase());
        }
    }
    public static class CustomComparatorByDateCreatedYoungestToOldest implements Comparator<Filenames>{

        @Override
        public int compare(Filenames lhs, Filenames rhs) {
            return (rhs).getCreationDate().compareTo(lhs.getCreationDate());
        }
    }
    public static class CustomComparatorByDateEditedYoungestToOldest implements Comparator<Filenames>{

        @Override
        public int compare(Filenames lhs, Filenames rhs) {
            return (rhs).getEditedDate().compareTo(lhs.getEditedDate());
        }
    }
}
