
package com.touwolf.esl;

/**
 * https://wiki.freeswitch.org/wiki/Hangup_causes
 */
public enum HangupCause
{
    /**
     * No other cause codes applicable.
     *
     * This is usually given by the router when none of the other codes apply.
     * This cause usually occurs in the same type of situations as cause 1, cause 88, and cause 100.
     */
    UNSPECIFIED(0, 0),
    /**
     * This cause is used when a called party does not respond to a call establishment message with either an alerting
     * or connect indication within the prescribed period of time allocated.
     */
    NO_USER_RESPONSE(18, 408),
    /**
     * This cause indicates that there is no appropriate circuit/channel presently available to handle the call.
     */
    NORMAL_CIRCUIT_CONGESTION(34,503),
    /**
     * This cause indicates that the switching equipment generating this cause is experiencing a period of high traffic.
     */
    SWITCH_CONGESTION(42, 503);
    /**
     * See ITU-T Q.850 standard for a formal definition of standard telephony disconnect cause codes for ISDN, and
     * the mapping between Q.931 (DSSS1) and ISUP codes.
     */
    private final Integer itu;

    /**
     * SIP Equiv.
     * https://wiki.freeswitch.org/wiki/SIP_Protocol_Messages
     */
    private final Integer sip;

    HangupCause(Integer itu, Integer sip)
    {
        this.itu = itu;
        this.sip = sip;
    }

    public Integer getItu()
    {
        return itu;
    }

    public Integer getSip()
    {
        return sip;
    }
}
