(ns moon.actor-fns.data-viewer-window
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.label :as label]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.scroll-pane :as scroll-pane]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.table :as gdx-table]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.text-button :as text-button]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.widget-group :as widget-group]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.window :as gdx-window]
            [clj.api.com.badlogic.gdx.scenes.scene2d.utils.change-listener :as change-listener]
            [moon.actor :as actor]
            [moon.stage :as stage]
            [moon.table :as table]
            [moon.window :as window]))

(defn create
  [{:keys [title
           data
           width
           height
           skin]}]
  {:pre [(map? data)]}
  (let [k->label-str (fn [k]
                       (str "[LIGHT_GRAY]:"
                            (when-let [ns (namespace k)] (str ns "/"))
                            "[][WHITE]"
                            (name k)
                            "[]"))
        v->text (fn [v]
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
                     (doto (text-button/create "Map" skin)
                       (actor/add-listener!
                        (change-listener/create
                         (fn [_event actor]
                           (stage/add-actor! (actor/stage actor)
                                             (create
                                              {:title "title"
                                               :data v
                                               :width 500
                                               :height 500
                                               :skin skin}))))))
                     (label/create (v->text v) skin)))
        rows (for [[k v] (sort-by key data)]
               {:label (k->label-str k)
                :actor (v->actor v skin)})
        scroll-pane-table (-> (gdx-table/create)
                              (table/add-rows! (for [{:keys [label actor]} rows]
                                                 [{:actor (label/create label skin)}
                                                  {:actor actor}])))
        scroll-pane-cell (let [table (doto (gdx-table/create)
                                       (table/set-cell-defaults! {:pad 1})
                                       (table/add-rows! [[scroll-pane-table]])
                                       (widget-group/pack!))]
                           {:actor (scroll-pane/create table skin)
                            :width width
                            ; (- (viewport/world-width viewport) 100)
                            ; (+ 100 (/ (viewport/world-width viewport) 2))
                            :height height
                            ; (- (viewport/world-height viewport) 200)
                            ; (- (viewport/world-height viewport) 50) #_(min (- (:height viewport) 50) (height table))
                            })]
    (doto (gdx-window/create title skin)
      (window/add-close-button! skin)
      (table/add-rows! [[scroll-pane-cell]])
      (widget-group/pack!))))
