/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.touwolf.esl;

/**
 *
 * @author Gilberto
 */
public interface EslMessage
{
    String get(String header);

    String getBody();
}
