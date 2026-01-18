(ns moon.ui-impl
  (:require [moon.ui :as ui]
            [moon.ui.data-viewer-window :as data-viewer-window]))

(defmethod ui/actor :ui/data-viewer-window
  [opts]
  (data-viewer-window/create opts))
