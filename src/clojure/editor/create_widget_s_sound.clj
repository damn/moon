(ns clojure.editor.create-widget-s-sound
  (:require [clojure.actor.add-listener]
            [clojure.table.add-rows :refer [add-rows!]]
            [clojure.editor.create-widget :refer [create-widget]]
            [clojure.editor.create-widget-open-select-sounds-handler :as open-select-sounds-handler]
            [clojure.editor.create-widget-sound-columns :as sound-columns]
            [clojure.event :as event]
            [clojure.ui-table :as table]
            [clojure.ui-text-button :as text-button]
            [clojure.utils-change-listener :as change-listener]))

(defmethod create-widget :s/sound
  [_ sound-name {:keys [ctx/skin]}]
  (let [table (table/create
               {:table/cell-defaults {:pad 5}})]
    (letfn [(sound-columns-fn [skin table sound-name]
              (sound-columns/sound-columns skin table sound-name open-select-fn))
            (open-select-fn [table]
              (open-select-sounds-handler/open-select-sounds-handler table sound-columns-fn))]
      (add-rows! table [(if sound-name
                           (sound-columns-fn skin table sound-name)
                           [{:actor
                             (doto (text-button/create {:text "No sound" :skin skin})
                               (clojure.actor.add-listener/f (change-listener/create
                                                        (fn [event _actor]
                                                          ((open-select-fn table)
                                                           (:stage/ctx (event/get-stage event)))))))}])])
      table)))
