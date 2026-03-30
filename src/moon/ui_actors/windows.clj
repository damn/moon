(ns moon.ui-actors.windows
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.group :as gdx-group]
            [moon.actor :as actor]
            [moon.group :as group]))

(defn create
  [ctx actors]
  (doto (gdx-group/create)
    (group/add-actors! (map (fn [[f & params]] (apply f ctx params)) actors))
    (actor/set-name! "moon.ui.windows")))
