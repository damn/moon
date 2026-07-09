(ns clojure.ui.data-viewer-window
  (:require [clojure.k-label-str :refer [k->label-str]]
            [clojure.scene2d.actor.add-listener]
            [clojure.scene2d.actor.get-stage]
            [clojure.scene2d.utils.change-listener :as change-listener]
            [clojure.stage :as stage]
            [clojure.ui-label :as label]
            [clojure.ui-scroll-pane :as scroll-pane]
            [clojure.ui-table :as table]
            [clojure.ui-text-button :as text-button]
            [clojure.ui-window :as window]
            [clojure.ui.window.add-close-button :as add-close-button]))

(defn create
  [{:keys [title
           data
           width
           height
           skin]}]
  {:pre [(map? data)]}
  (let [v->actor (fn [v skin]
                   (if (map? v)
                     (doto (text-button/create {:text "Map" :skin skin})
                       (clojure.scene2d.actor.add-listener/f (change-listener/create
                                                              (fn [_event actor]
                                                                (stage/add-actor! (clojure.scene2d.actor.get-stage/f actor)
                                                                                  (create
                                                                                   {:title "title"
                                                                                    :data v
                                                                                    :width 500
                                                                                    :height 500
                                                                                    :skin skin}))))))
                     (label/create
                      {:text (cond
                              (or (keyword? v)
                                  (number? v)
                                  (boolean? v)
                                  (string? v))
                              (str "[GOLD]" v "[]")

                              :else
                              (str (class v)))
                       :skin skin})))
        rows (for [[k v] (sort-by key data)]
               {:label (k->label-str k)
                :actor (v->actor v skin)})
        scroll-pane-table (table/create
                           {:table/rows (for [{:keys [label actor]} rows]
                                          [{:actor (label/create {:text label
                                                                  :skin skin})}
                                           {:actor actor}])})

        ; TODO use moon.scroll-pane-cell
        scroll-pane-cell (let [table (table/create {:table/cell-defaults {:pad 1}
                                                    :table/rows [[scroll-pane-table]]})]
                           {:actor (scroll-pane/create
                                    {:actor table
                                     :skin skin})
                            :width width
                            ; (- (:viewport/world-width viewport) 100)
                            ; (+ 100 (/ (:viewport/world-width viewport) 2))
                            :height 800
                            ; (- (:viewport/world-height viewport) 200)
                            ; (- (:viewport/world-height viewport) 50) #_(min (- (:height viewport) 50) (height table))
                            })]
    (doto (window/create
           {:title title
            :skin skin
            :table/rows [[scroll-pane-cell]]})
      (add-close-button/f! skin))))
