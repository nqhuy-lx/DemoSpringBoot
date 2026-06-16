package com.hnq.repository.criteria;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteria {
    private String key; // firstName, lastName, id ... (column)
    private String operation; // : , < , >
    private Object value;
}
