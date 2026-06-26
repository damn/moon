(ns scene2d.ui.label
  (:require [com.badlogic.gdx.scenes.scene2d.ui.label :as label]))

(defn create
  [{:keys [text skin]}]
  (label/create text skin))
