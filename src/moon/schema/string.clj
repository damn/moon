(ns moon.schema.string
  (:require [clojure.scene2d.actor :as actor]
            [clojure.scene2d.ui.text-field :as text-field]))

(defn malli-form [_ _schemas]
  :string)

(defn create [schema v {:keys [ctx/skin]}]
  (actor/create
   {:type :ui/text-field
    :text (str v)
    :skin skin
    :actor/listeners {:listener/text-tooltip [(str schema) skin]}}))

(defn value [_ widget _schemas]
  (text-field/text widget))
