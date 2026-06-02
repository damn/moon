(ns gdx.scenes.scene2d.ui.data-viewer-window
  (:require [gdx.scenes.scene2d.ui.label :as label]
            [clojure.gdx.scene2d.ui.scroll-pane :as scroll-pane]
            [gdx.scenes.scene2d.ui.table :as table]
            [clojure.gdx.scene2d.ui.text-button :as text-button]
            [gdx.scenes.scene2d.ui.window :as window]
            [clojure.gdx.scene2d.utils.change-listener :as change-listener]
            [gdx.scenes.scene2d.ui.data-viewer-window.k-label-str :refer [k->label-str]]
            [gdx.stage :as stage]))

(defn create
  [{:keys [title
           data
           width
           height
           skin]}]
  {:pre [(map? data)]}
  (let [v->text (fn [v]
                  (cond
                   (or (keyword? v)
                       (number? v)
                       (boolean? v)
                       (string? v))
                   (str "[GOLD]" v "[]")

                   :else
                   (str (class v))))
        v->actor (fn [v skin]
                   (if (map? v)
                     (text-button/create
                      {:text "Map"
                       :skin skin
                       :actor/listeners [(change-listener/create
                                          (fn [_event actor]
                                            (stage/add-actor! (.getStage actor)
                                                              (create
                                                               {:title "title"
                                                                :data v
                                                                :width 500
                                                                :height 500
                                                                :skin skin}))))]})
                     (label/create
                      {:text (v->text v)
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
    (window/create
     {:title title
      :skin skin
      :table/rows [[scroll-pane-cell]]
      :window/close-button? skin})))
