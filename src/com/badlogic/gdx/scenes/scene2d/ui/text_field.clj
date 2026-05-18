(ns com.badlogic.gdx.scenes.scene2d.ui.text-field
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               TextField)))

(defn create
  [{:keys [text skin]}]
  (TextField. ^String text ^Skin skin))

(def text TextField/.getText)
