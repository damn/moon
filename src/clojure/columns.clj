(ns clojure.columns
  (:require
            [clojure.add-listener]
            [clojure.event :as event]
            [clojure.open-select-sounds-handler :refer [open-select-sounds-handler]]
            [clojure.ctx-do :refer [do!]]
            [clojure.ui-text-button :as text-button]
            [clojure.utils-change-listener :as change-listener]))

(defn sound-columns [skin table sound-name]
  [{:actor (doto (text-button/create
                  {:text sound-name
                   :skin skin})
             (clojure.add-listener/f (change-listener/create
                              (fn [event _actor]
                                ((open-select-sounds-handler table sound-columns)
                                 (:stage/ctx (event/get-stage event)))))))}
   {:actor (doto (text-button/create
                  {:text "play!"
                   :skin skin})
             (clojure.add-listener/f (change-listener/create
                              (fn [event _actor]
                                (do! (:stage/ctx (event/get-stage event))
                                     [[:tx/sound sound-name]])))))}])
