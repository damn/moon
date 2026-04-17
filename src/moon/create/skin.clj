(ns moon.create.skin
  (:require [clojure.gdx.scene2d.ui.skin :as skin]
            [clojure.bitmap-font :as bitmap-font]
            [clojure.files :as files]))

(defn step
  [{:keys [ctx/files]
    :as ctx}
   path]
  (assoc ctx :ctx/skin (let [skin (skin/create (files/internal files path))]
                         (bitmap-font/enable-markup! (skin/font skin "default-font") true)
                         skin)))
