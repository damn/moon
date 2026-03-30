(ns clj.api.com.badlogic.gdx.scenes.scene2d.ui.table
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Table)))

(defn add! [^Table table ^Actor actor]
  (.add table actor))
