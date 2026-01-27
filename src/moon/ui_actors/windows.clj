(ns moon.ui-actors.windows
  (:require [moon.ui.group :as group])
  (:import (com.badlogic.gdx.scenes.scene2d Group)))

(defn create
  [ctx actors]
  (doto (Group.)
    (group/add-actors! (map (fn [[f & params]] (apply f ctx params)) actors))
    (.setName "moon.ui.windows")))
