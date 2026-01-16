(ns moon.ui-actors.windows
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.group :as group]))

(defn create
  [ctx actors]
  (group/create
   {:actor/name "moon.ui.windows"
    :group/actors (map (fn [[f & params]] (apply f ctx params)) actors)}))
