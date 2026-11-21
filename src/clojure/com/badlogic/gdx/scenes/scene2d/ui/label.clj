(ns clojure.com.badlogic.gdx.scenes.scene2d.ui.label
  (:import (com.badlogic.gdx.scenes.scene2d.ui Label Skin)))

(defn create [^String text ^Skin skin]
  (Label. text skin))

(defn set-text! [^Label label ^String text]
  (.setText label text))

(defn label? [x]
  (instance? Label x))
