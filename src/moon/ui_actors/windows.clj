(ns moon.ui-actors.windows
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.group :as gdx-group]
            [moon.group :as group])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn create
  [ctx actors]
  (doto (gdx-group/create)
    (group/add-actors! (map (fn [[f & params]] (apply f ctx params)) actors))
    (Actor/.setName "moon.ui.windows")))
