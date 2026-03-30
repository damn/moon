(ns moon.schema.string
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.text-tooltip :as text-tooltip])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               TextField)))

(defn malli-form [_ _schemas]
  :string)

(defn create [schema v {:keys [ctx/skin]}]
  (doto (TextField. (str v) ^Skin skin)
    (.addListener (text-tooltip/create (str schema) skin))))

(defn value [_ widget _schemas]
  (TextField/.getText widget))
