(ns cdq.ui.widget-group
  (:require [cdq.ui.group :as group]
            [moon.scene2d.ui.widget-group :as widget-group]))

(defn set-opts!
  [widget-group {:keys [fill-parent? pack?] :as opts}]
  (when fill-parent?
    (widget-group/set-fill-parent! widget-group fill-parent?))
  (when pack?
    (widget-group/pack! widget-group))
  (group/set-opts! widget-group opts))
