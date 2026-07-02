(ns ctx.skin
  (:require [clojure.gdx.files.internal :as internal])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin)))

(defn step
  [{:keys [ctx/files]}]
  (let [skin (Skin. (internal/f files "skin/uiskin.json"))]
    (set! (.markupEnabled (.getData (.getFont skin "default-font"))) true)
    skin))
