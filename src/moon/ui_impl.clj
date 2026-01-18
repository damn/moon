(ns moon.ui-impl
  (:require [moon.ui :as ui]
            [moon.ui.data-viewer-window :as data-viewer-window]
            [moon.ui.property-overview-window :as property-overview-window]
            [moon.ui.property-editor-window :as property-editor-window]
            [moon.ui.window :as window]))

(defmethod ui/actor :ui/data-viewer-window
  [opts]
  (data-viewer-window/create opts))

(defmethod ui/actor :ui/property-overview-window
  [opts]
  (property-overview-window/create opts))

(defmethod ui/actor :ui/window
  [opts]
  (window/create opts))

(defmethod ui/actor :ui/property-editor-window
  [opts]
  (property-editor-window/create opts))
