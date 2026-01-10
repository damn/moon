(ns moon.ui.widget-group
  (:require [gdl.ui.widget-group :as widget-group]
            [moon.ui.group :as group]))

(defn set-opts!
  [widget-group {:keys [fill-parent? pack?] :as opts}]
  (when fill-parent?
    (widget-group/set-fill-parent! widget-group fill-parent?))
  (when pack?
    (widget-group/pack! widget-group))
  (group/set-opts! widget-group opts))
