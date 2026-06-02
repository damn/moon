(ns clojure.gdx.scene2d.ui.widget-group.set-opts
  (:require [clojure.gdx.scene2d.ui.widget-group :as widget-group]
            [clojure.gdx.scene2d.group.set-opts :as group]))

(defn set-opts! [widget-group opts]
  (when (contains? opts :widget-group/fill-parent?)
    (widget-group/set-fill-parent! widget-group (:widget-group/fill-parent? opts)))
  (group/set-opts! widget-group opts))
