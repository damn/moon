(ns moon.data-viewer-window
  (:require [clojure.k-label-str :refer [k->label-str]]
            [clojure.table-set-opts :as table-set-opts]
            [moon.window :refer [add-close-button!]]
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [com.badlogic.gdx.scenes.scene2d.stage :as stage]
            [com.badlogic.gdx.scenes.scene2d.ui.label :as label]
            [com.badlogic.gdx.scenes.scene2d.ui.scroll-pane :as scroll-pane]
            [com.badlogic.gdx.scenes.scene2d.ui.table :as table]
            [com.badlogic.gdx.scenes.scene2d.ui.text-button :as text-button]
            [com.badlogic.gdx.scenes.scene2d.ui.window :as window]
            [com.badlogic.gdx.scenes.scene2d.utils.change-listener :as change-listener]))

(defn create
  [{:keys [title
           data
           width
           height
           skin]}]
  {:pre [(map? data)]}
  (let [v->actor (fn [v skin]
                   (if (map? v)
                     (doto (text-button/new "Map" skin)
                       (actor/addListener (change-listener/create
                                            (fn [_event actor]
                                              (stage/addActor (actor/getStage actor)
                                                              (create
                                                               {:title "title"
                                                                :data v
                                                                :width 500
                                                                :height 500
                                                                :skin skin}))))))
                     (label/new (cond
                                  (or (keyword? v)
                                      (number? v)
                                      (boolean? v)
                                      (string? v))
                                  (str "[GOLD]" v "[]")

                                  :else
                                  (str (class v)))
                                skin)))
        rows (for [[k v] (sort-by key data)]
               {:label (k->label-str k)
                :actor (v->actor v skin)})
        scroll-pane-table (doto (table/new)
                            (table-set-opts/set-opts! {:table/rows (for [{:keys [label actor]} rows]
                                                                    [{:actor (label/new label skin)}
                                                                     {:actor actor}])}))
        scroll-pane-cell (let [table (doto (table/new)
                                         (table-set-opts/set-opts! {:table/cell-defaults {:pad 1}
                                                                    :table/rows [[scroll-pane-table]]}))]
                           {:actor (scroll-pane/new table skin)
                            :width width
                            :height 800})]
    (doto (doto (window/new title skin)
                (table-set-opts/set-opts! {:title title
                                           :skin skin
                                           :table/rows [[scroll-pane-cell]]}))
          (add-close-button! skin))))
