(ns clojure.ui.window.add-close-button
  (:require
            [gdl.actor :as actor] [clojure.window :as window]
            [clojure.scene2d.utils.change-listener :as change-listener]
            [clojure.ui.table.add-cell :refer [add-cell!]]
            [clojure.ui-text-button :as text-button]))

(defn f! [window skin]
  (add-cell! (window/get-title-table window)
             {:actor (doto (text-button/create {:text "X" :skin skin})
                       (actor/add-listener (change-listener/create
                                       (fn [_event _actor]
                                         (actor/remove-actor window)))))}))
