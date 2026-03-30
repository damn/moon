(ns moon.schema.string
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.text-field :as text-field]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.text-tooltip :as text-tooltip]
            [moon.actor :as actor]))

(defn malli-form [_ _schemas]
  :string)

(defn create [schema v {:keys [ctx/skin]}]
  (doto (text-field/create (str v) skin)
    (actor/add-listener! (text-tooltip/create (str schema) skin))))

(defn value [_ widget _schemas]
  (text-field/text widget))
