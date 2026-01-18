(ns moon.schema.string
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               TextField
                                               TextTooltip)))

(defn malli-form [_ _schemas]
  :string)

(defn create [schema v {:keys [^Skin ctx/skin]}]
  (doto (TextField. (str v) skin)
    (.addListener (TextTooltip. (str schema) skin))))

(defn value [_ widget _schemas]
  (TextField/.getText widget))
