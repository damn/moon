(ns ctx.skin
  (:require [clojure.gdx.bitmap-font.get-data :as get-data]
            [clojure.gdx.bitmap-font$bitmap-font-data.set-markup-enabled :as set-markup-enabled]
            [clojure.gdx.files.internal :as internal]
            [clojure.gdx.skin.new :as skin]
            [clojure.gdx.skin.get-font :as get-font]))

(defn step
  [{:keys [ctx/files]}]
  (let [skin (skin/f (internal/f files "skin/uiskin.json"))]
    (-> skin
        (get-font/f "default-font")
        get-data/f
        (set-markup-enabled/f true))
    skin))
