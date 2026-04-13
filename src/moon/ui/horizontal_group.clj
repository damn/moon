(ns moon.ui.horizontal-group
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.horizontal-group :as horizontal-group]
            [moon.ui.actor :as actor]))

(defn create [{:keys [space pad] :as opts}]
  (doto (horizontal-group/create)
    (horizontal-group/space! space)
    (horizontal-group/pad! pad)
    (actor/set-opts! opts)))
