(ns moon.ui.label
  (:import (com.badlogic.gdx.scenes.scene2d.ui Label
                                               Skin)))

(defn create
  [{:keys [label/text
           label/skin]}]
  (Label. ^String text ^Skin skin))
