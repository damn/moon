(ns editor.create-widget.val-max
  (:require [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [clojure.edn-str :refer [->edn-str]]
            [gdx.scene2d.ui.text-field :as text-field]
            [gdx.scene2d.ui.text-tooltip :as text-tooltip]))

(defn f
  [schema v {:keys [ctx/skin]}]
  (doto (text-field/create (->edn-str v) skin)
    (actor/add-listener! (text-tooltip/create (str schema) skin))))
