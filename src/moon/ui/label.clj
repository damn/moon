(ns moon.ui.label
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.label :as label]))

(defn create
  [{:keys [text skin]}]
  (label/create text skin))
