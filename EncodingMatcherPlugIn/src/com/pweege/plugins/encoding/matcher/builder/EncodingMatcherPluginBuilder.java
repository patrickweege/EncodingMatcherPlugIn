package com.pweege.plugins.encoding.matcher.builder;

import java.io.InputStream;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import com.ibm.icu.text.CharsetMatch;
import com.pweege.plugins.encoding.matcher.util.EncodingMatcherUtil;
import com.pweege.plugins.encoding.matcher.util.ICharsetDetecdor;
import com.pweege.plugins.encoding.matcher.util.JUniversalChardet_CharsetDetector;
import com.pweege.plugins.encoding.matcher.util.PreferencesPathMatcher;

public class EncodingMatcherPluginBuilder extends IncrementalProjectBuilder {

	class IncrementalBuildDeltaVisitor implements IResourceDeltaVisitor {

		private final PreferencesPathMatcher preferencesMatcher;

		public IncrementalBuildDeltaVisitor() {
			this.preferencesMatcher = new PreferencesPathMatcher();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.
		 * core.resources.IResourceDelta)
		 */
		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource resource = delta.getResource();

			boolean canVisit = preferencesMatcher.matchesPreferences(resource);
			if (canVisit) {
				switch (delta.getKind()) {
				case IResourceDelta.ADDED:
					checkEncoding(resource);
					break;
				case IResourceDelta.REMOVED:
					// handle removed resource
					break;
				case IResourceDelta.CHANGED:
					checkEncoding(resource);
					break;
				}
			}
			// return true to continue visiting children.
			return true;
		}
	}

	class FullBuildResourceVisitor implements IResourceVisitor {

		private final PreferencesPathMatcher preferencesMatcher;

		public FullBuildResourceVisitor() {
			preferencesMatcher = new PreferencesPathMatcher();
		}

		public boolean visit(IResource resource) {
			boolean canVisit = preferencesMatcher.matchesPreferences(resource);
			if (canVisit) {
				checkEncoding(resource);
			}
			return true;
		}
	}

	public static final String BUILDER_ID = "EncodingMatcherPlugIn.encodingMatcherPluginBuilder";

	private static final String MARKER_TYPE = "EncodingMatcherPlugIn.encodingProblem";

	private void addMarker(IFile file, String message, int lineNumber, int severity) {
		try {
			IMarker marker = file.createMarker(EncodingMatcherPluginBuilder.MARKER_TYPE);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			if (lineNumber == -1) {
				lineNumber = 1;
			}
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
	 * java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
		if (kind == FULL_BUILD) {
			fullBuild(monitor);
		} else {
			IResourceDelta delta = getDelta(getProject());
			if (delta == null) {
				fullBuild(monitor);
			} else {
				incrementalBuild(delta, monitor);
			}
		}
		return null;
	}

	protected void clean(IProgressMonitor monitor) throws CoreException {
		// delete markers set and files created
		getProject().deleteMarkers(MARKER_TYPE, true, IResource.DEPTH_INFINITE);
	}

	void checkEncoding(IResource resource) {
		try {
			if (resource instanceof IFile) {
				IFile file = (IFile) resource;
				String currentCharset = this.getCurrentCharset(file);

				InputStream contents = file.getContents();

				ICharsetDetecdor detector = new JUniversalChardet_CharsetDetector();
				String detectedCharset = detector.detectCharset(contents);
				
				boolean isCurrentEqualsDetected = EncodingMatcherUtil.areCharsetsEqual(currentCharset, detectedCharset);
				
				if(!isCurrentEqualsDetected) {
					
					contents = file.getContents();
					CharsetMatch[] detectCharsets = EncodingMatcherUtil.detectCharsets(contents);

					StringBuilder msg = new StringBuilder();
					
					msg.append("Maybe is the file not decodable with '").append(currentCharset).append("'. ");
					msg.append("Try '").append(detectedCharset).append("'");
					
					int confidence = EncodingMatcherUtil.getConfidence(detectCharsets, detectedCharset);
					if(confidence > 0) {
						msg.append(confidence).append("%");
					}
					
					addMarker(file, msg.toString(), -1, IMarker.SEVERITY_WARNING);
					
				} else {
					deleteMarkers(file);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void deleteMarkers(IFile file) {
		try {
			file.deleteMarkers(MARKER_TYPE, false, IResource.DEPTH_ZERO);
		} catch (CoreException ce) {

		}
	}

	protected void fullBuild(final IProgressMonitor monitor) throws CoreException {
		try {
			getProject().accept(new FullBuildResourceVisitor());
		} catch (CoreException e) {
		}
	}

	protected void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor) throws CoreException {
		// the visitor does the work.
		delta.accept(new IncrementalBuildDeltaVisitor());
	}

	private String getCurrentCharset(IFile file) throws CoreException {
		String theCharset = null;
		theCharset = file.getCharset(false);
		if (theCharset == null) {
			IContainer container = file.getParent();
			theCharset = container.getDefaultCharset(true);
		}
		return theCharset;
	}

}
