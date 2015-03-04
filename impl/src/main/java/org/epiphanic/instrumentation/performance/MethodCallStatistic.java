/*
 * org-epiphanic-libraries-performance
 * Copyright 2010 Epiphanic Networks
 *
 * This source is licensed under the terms of the MIT license. Please see the distributed license.txt for details.
 */

package org.epiphanic.instrumentation.performance;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


/**
 * Holds information about a given method call, including a name for the operation, a start and end time and metadata.
 * If the method was not successful (an exception was thrown), this will be recorded as well. An optional User ID that
 * can be associated with the method call is also recorded.<p/>
 *
 * ID generation is set to "Auto" which should be the equivalent of "Native" - this should have the widest arity of
 * supported database back-ends, but you might have to tweak this a bit. You might also want to change the sequence name
 * to be something more in line with whatever you're using.<p/>
 *
 * This class is non-final for testing purposes.<p/>
 *
 * @author Greg Feigenson
 */
@Entity
@Table(name = "METHOD_PERFORMANCE")
@SequenceGenerator(
		name = "performanceInstrumentationGenerator",
		sequenceName = "METHOD_PERFORMANCE_SEQ"
)
public class MethodCallStatistic implements IMethodCallStatistic
{
	/**
	 * Our primary key.
	 */
	private Long _id;

	/**
	 * Holds an optional ID of the user executing the method.
	 */
	private Long _userId;

	/**
	 * Holds a string representing a name for the operation.
	 */
	private String _operationName;

	/**
	 * Holds when the operation started, useful for calculating execution time for the method.
	 */
	private Date _operationStart;

	/**
	 * Holds when the operation completed, useful for calculating execution time for the method.
	 */
	private Date _operationCompletion;

	/**
	 * Holds optional metadata about the operation. May include information such as a stack trace if the method fails.
	 */
	private String _metaData;

	/**
	 * Holds whether or not the method successfully completed, as defined by not throwing an exception (checked or
	 * runtime).
	 */
	private boolean _successful;


	@Override
	@Id
	@Column(name = "METRIC_ID")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "performanceInstrumentationGenerator")
	public Long getId()
	{
		return _id;
	}


	public void setId(final Long id)
	{
		_id = id;
	}


	/**
	 * Gets an optional user ID that may be associated with the method call.
	 *
	 * @return The ID of the user that executed the method call, or <code>null</code> if none is available.
	 */
	@Override
	@Column(name = "USER_ID")
	public Long getUserId()
	{
		return _userId;
	}


	/**
	 * Sets an optional user ID of the user that is associated with the method call.
	 *
	 * @param userId The ID of the user that executed the method call, or <code>null</code> if none is available.
	 */
	public void setUserId(final Long userId)
	{
		_userId = userId;
	}


	@Override
	@Column(name = "OPERATION_NAME")
	public String getOperationName()
	{
		return _operationName;
	}


	public void setOperationName(final String operationName)
	{
		_operationName = operationName;
	}


	@Override
	@Column(name = "START_TIME")
	public Date getOperationStart()
	{
		return _operationStart;
	}


	public void setOperationStart(final Date operationStart)
	{
		_operationStart = operationStart;
	}


	@Override
	@Column(name = "END_TIME")
	public Date getOperationCompletion()
	{
		return _operationCompletion;
	}


	public void setOperationCompletion(final Date operationCompletion)
	{
		_operationCompletion = operationCompletion;
	}


	@Override
	@Column(name="METADATA")
	public String getMetaData()
	{
		return _metaData;
	}


	public void setMetaData(final String metaData)
	{
		_metaData = metaData;
	}


	@Override
	@Column(name="SUCCESS")
	public boolean isOperationSuccessful()
	{
		return !_successful;
	}


	public void setOperationSuccessful(final boolean successful)
	{
		_successful = !successful;
	}
}
