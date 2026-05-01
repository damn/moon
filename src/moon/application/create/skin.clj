(ns moon.application.create.skin
  (:require [clojure.gdx.scene2d.ui.skin :as skin])
  (:import (com.badlogic.gdx Application)))

(defn step
  [{:keys [^Application ctx/app]
    :as ctx}]
  (assoc ctx :ctx/skin (let [skin (skin/create (.internal (.getFiles app) "uiskin.json"))]
                         (set! (.markupEnabled (.getData (skin/font skin "default-font"))) true)
                         skin)))
