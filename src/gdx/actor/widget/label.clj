(ns gdx.actor.widget.label
  (:refer-clojure :exclude [class])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Label Skin)))

(def class Label)

(defn create [text skin]
  (Label. ^String text ^Skin skin))

(defn set-text! [label text]
  (.setText ^Label label ^String text))
