(ns com.badlogic.gdx.scenes.scene2d.ui.label
  (:refer-clojure :exclude [class new])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Label Skin)))

(def class Label)

(defn new [^String text ^Skin skin]
  (Label. text skin))

(defn set-text! [^Label label ^String text]
  (Label/.setText label text))
