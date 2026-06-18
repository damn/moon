(ns editor.widget.enum
  (:require [clojure.edn-str :refer [->edn-str]]
            [clojure.edn :as edn]
            [com.badlogic.gdx.scenes.scene2d.ui.select-box :as select-box]))

(defn create [schema v {:keys [ctx/skin]}]
  (select-box/create
   {:skin skin
    :items (map ->edn-str (rest schema))
    :selected (->edn-str v)}))

(defn value [_  widget _schemas]
  (edn/read-string (select-box/selected widget)))
