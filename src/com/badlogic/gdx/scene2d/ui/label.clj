(ns com.badlogic.gdx.scene2d.ui.label
  (:import (com.badlogic.gdx.scenes.scene2d.ui Label
                                               Skin)))

(defn create [{:keys [text skin]}]
  (Label. ^String text ^Skin skin))

(defn set-text! [^Label label ^String text]
  (.setText label text))
