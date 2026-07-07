(ns clojure.create-widget-sound
  (:require [clojure.actor :as actor]
            [clojure.event :as event]
            [clojure.columns :refer [sound-columns]]
            [clojure.open-select-sounds-handler :refer [open-select-sounds-handler]]
            [clojure.ui-table :as table]
            [clojure.add-rows :refer [add-rows!]]
            [clojure.ui-text-button :as text-button]
            [clojure.utils-change-listener :as change-listener]))

(defn f
  [_  sound-name {:keys [ctx/skin]}]
  (let [table (table/create
               {:table/cell-defaults {:pad 5}})]
    (add-rows! table [(if sound-name
                        (sound-columns skin table sound-name)
                        [{:actor
                          (doto (text-button/create {:text "No sound" :skin skin})
                            (actor/add-listener! (change-listener/create
                                             (fn [event _actor]
                                               ((open-select-sounds-handler table)
                                                (:stage/ctx (event/get-stage event))
                                                sound-columns)))))}])])
    table))
