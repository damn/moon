(ns moon.data-viewer-window
  (:require [moon.table :as table]
            [moon.text-button :as text-button]
            [moon.window :as window])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Label
                                               ScrollPane
                                               Skin
                                               Window)))

(defn- k->label-str [k]
  (str "[LIGHT_GRAY]:"
       (when-let [ns (namespace k)] (str ns "/"))
       "[][WHITE]"
       (name k)
       "[]"))

(defn- v->text [v]
  (cond
   (or (keyword? v)
       (number? v)
       (boolean? v)
       (string? v))
   (str "[GOLD]" v "[]")

   :else
   (str (class v))))

(declare create)

(defn- v->actor-decl [v skin]
  (if (map? v)
    (text-button/create
     {:text "Map"
      :on-clicked (fn [actor _ctx]
                    (.addActor (Actor/.getStage actor)
                               (create
                                {:title "title"
                                 :data v
                                 :width 500
                                 :height 500
                                 :skin skin})))
      :skin skin})
    (Label. ^String (v->text v) ^Skin skin)))

(defn create
  [{:keys [^String title
           data
           width
           height
           ^Skin skin]}]
  {:pre [(map? data)]}
  (let [rows (for [[k v] (sort-by key data)]
               {:label (k->label-str k)
                :actor (v->actor-decl v skin)})
        scroll-pane-table (table/create
                           {:rows (for [{:keys [label actor]} rows]
                                    [{:actor (Label. ^String label ^Skin skin)}
                                     {:actor actor}])})
        scroll-pane-cell (let [table (doto (table/create
                                            {:rows [[scroll-pane-table]]
                                             :cell-defaults {:pad 1}})
                                       (.pack))]
                           {:actor (ScrollPane. table skin)
                            :width width ; (- (viewport/world-width viewport) 100) ; (+ 100 (/ (viewport/world-width viewport) 2))
                            :height height ; (- (viewport/world-height viewport) 200) ; (- (viewport/world-height viewport) 50) #_(min (- (:height viewport) 50) (height table))
                            })]
    (doto (Window. title skin)
      (window/add-close-button! skin)
      (table/set-opts! {:rows [[scroll-pane-cell]]})
      (.pack))))
