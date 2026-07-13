(ns clojure.gdx.scenes.scene2d.ui.skin
  (:require [com.badlogic.gdx.scenes.scene2d.ui.skin :as skin]))

(defn create [file-handle]
  (skin/new file-handle))

(defn get-font [skin font-name]
  (skin/getFont skin font-name))
