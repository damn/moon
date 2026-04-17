(ns moon.schema.string
  (:require [clojure.scene2d.ui.text-field :as text-field]
            [moon.ui :as ui]))

(defn malli-form [_ _schemas]
  :string)

(defn create [schema v {:keys [ctx/skin]}]
  (ui/create
   {:type :ui/text-field
    :text (str v)
    :skin skin
    :actor/listeners {:listener/text-tooltip [(str schema) skin]}}))

(defn value [_ widget _schemas]
  (text-field/text widget))
