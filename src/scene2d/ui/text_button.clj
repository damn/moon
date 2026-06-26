(ns scene2d.ui.text-button
  (:require [com.badlogic.gdx.scenes.scene2d.ui.text-button :as text-button]))

(defn create
  [{:keys [text skin]}]
  (text-button/create text skin))
