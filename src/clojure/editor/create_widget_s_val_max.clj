(ns clojure.editor.create-widget-s-val-max
  (:require [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [clojure.editor.create-widget :refer [create-widget]]
            [clojure.edn.v-to-str :refer [->edn-str]]
            [com.badlogic.gdx.scenes.scene2d.ui.text-field :as text-field]
            [com.badlogic.gdx.scenes.scene2d.ui.text-tooltip :as text-tooltip]
            [clojure.tooltip :as tooltip]))

(defmethod create-widget :s/val-max
  [schema v {:keys [ctx/skin]}]
  (doto (text-field/new (->edn-str v) skin)
    (actor/addListener (text-tooltip/new (str schema) skin))))
