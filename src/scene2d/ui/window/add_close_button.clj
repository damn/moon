(ns scene2d.ui.window.add-close-button
  (:require [clojure.gdx :as gdx]
            [scene2d.utils.change-listener :as change-listener]
            [scene2d.ui.table.add-cell :refer [add-cell!]]
            [scene2d.ui.text-button :as text-button]))

(defn f! [window skin]
  (add-cell! (gdx/window-get-title-table window)
             {:actor (doto (text-button/create {:text "X" :skin skin})
                       (gdx/add-listener! (change-listener/create
                                           (fn [_event _actor]
                                             (gdx/remove! window)))))}))
