(ns clojure.gdx.table.add
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Table)))

(defn f [^Table table ^Actor actor]
  (Table/.add table actor))
