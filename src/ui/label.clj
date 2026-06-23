(ns ui.label
  (:refer-clojure :exclude [class])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Label
                                               Skin)))

(def class Label)

(defn create
  [{:keys [text skin]}]
  (Label. ^String text ^Skin skin))

(defn set-text! [^Label label text]
  (.setText label ^String text))
