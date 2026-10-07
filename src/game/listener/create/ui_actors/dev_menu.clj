(ns game.listener.create.ui-actors.dev-menu
  (:require [clojure.string :as str]
            [moon.level.modules :as modules]
            [moon.level.tmx :as tmx]
            [moon.level.uf-caves :as uf-caves])
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.scenes.scene2d Actor Event Group Stage Touchable)
           (com.badlogic.gdx.scenes.scene2d.ui Image Label Skin Table TextButton Window)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener)))

(def controls-info
  (str/join "\n"
            ["[W][A][S][D] - Move"
             "[ESCAPE] - Close windows"
             "[I] - Inventory window"
             "[E] - Entity Info window"
             "[-]/[=] - Zoom"
             "[P]/[SPACE] - Unpause"
             "Leftmouse click - use skill/drop item on cursor"]))

(def help-menu-item
  {:label "Help"
   :items [{:label controls-info}]})

(defn debug-flags-menu-item
  [show-tile-grid? show-cell-entities? show-cell-occupied? show-body-bounds? show-potential-field-colors?]
  {:label "Debug"
   :items [{:label "Toggle show-tile-grid?"
            :on-click #(swap! show-tile-grid? not)}
           {:label "Toggle show-cell-entities?"
            :on-click #(swap! show-cell-entities? not)}
           {:label "Toggle show-cell-occupied?"
            :on-click #(swap! show-cell-occupied? not)}
           {:label "Toggle show-body-bounds?"
            :on-click #(swap! show-body-bounds? not)}
           {:label "Potential field colors: off"
            :on-click #(reset! show-potential-field-colors? nil)}
           {:label "Potential field colors: :good"
            :on-click #(reset! show-potential-field-colors? :good)}
           {:label "Potential field colors: :evil"
            :on-click #(reset! show-potential-field-colors? :evil)}]})

(def select-world-menu-item
  {:label "Select World"
   :items (for [[label world-fn] [["Vampire" tmx/vampire]
                                  ["UF Caves" uf-caves/create]
                                  ["Modules" modules/create]]]
            {:label (str "Start " label)
             :on-click (fn []
                         #_(let [rebuild-actors! nil
                                 #_(fn rebuild-actors! [stage ctx]
                                     (.clear stage)
                                     ((requiring-resolve 'game.create.add-actors/step) ctx))
                                 create-world nil
                                 #_(requiring-resolve 'game.create.world/step)
                                 ui stage
                                 stage (:ctx/stage actor)]
                             (rebuild-actors! ui ctx)
                             #_(Disposable/.dispose @tiled-map)
                             (set! (.ctx ^Stage stage) (create-world ctx world-fn)))
                         nil)})})

(defn dev-menus
  [show-tile-grid? show-cell-entities? show-cell-occupied? show-body-bounds? show-potential-field-colors?]
  [(debug-flags-menu-item show-tile-grid? show-cell-entities? show-cell-occupied? show-body-bounds? show-potential-field-colors?)
   help-menu-item
   select-world-menu-item])

(defn- set-label-text-actor [label-widget text-fn]
  (proxy [Actor] []
    (act [delta]
      (when (.getStage ^Actor this)
        (.setText ^Label label-widget ^String (text-fn)))
      (let [^Actor this this]
        (proxy-super act delta)))
    (draw [batch parent-alpha])))

(defn- add-upd-label!
  ([skin table text-fn icon]
   (let [label (Label. "" ^Skin skin)
         sub-table (Table.)]
     (doseq [cell [{:actor (Image. ^Texture icon)}
                   {:actor label}]]
       (.add ^Table sub-table ^Actor (:actor cell)))
     (.row ^Table sub-table)
     (.pack sub-table)
     (.addActor ^Group table (set-label-text-actor label text-fn))
     (doto (.add ^Table table ^Actor sub-table)
       (.right)
       (.expandX))))
  ([skin table text-fn]
   (let [label (Label. "" ^Skin skin)]
     (.addActor ^Group table (set-label-text-actor label text-fn))
     (doto (.add ^Table table ^Actor label)
       (.right)
       (.expandX)))))

(defn- dev-menu-main-table [^Skin skin menus update-labels]
  (let [table (Table.)]
    (doseq [cell (for [{:keys [label items]} menus]
                   {:actor
                    (doto (TextButton. ^String label skin)
                      (.addListener (proxy [ChangeListener] []
                                      (changed [event actor]
                                        (let [^Stage stage (.getStage ^Event event)]
                                          (.addActor stage
                                                     (let [window (Window. ^String label skin)]
                                                       (doseq [{:keys [label on-click]} items]
                                                         (.add window (doto (TextButton. ^String label skin)
                                                                        (.addListener (proxy [ChangeListener] []
                                                                                        (changed [_event _actor]
                                                                                          (on-click)))))))
                                                       (.row window)
                                                       (.pack window)
                                                       (.add (.getTitleTable window)
                                                             (doto (TextButton. "X" skin)
                                                               (.addListener (proxy [ChangeListener] []
                                                                               (changed [_event _actor]
                                                                                 (.remove window))))))
                                                       window)))))))})]
      (.add table ^Actor (:actor cell)))
    (.row table)
    (.pack table)
    (doseq [{:keys [label update-fn icon]} update-labels]
      (let [update-fn #(str label ": " (update-fn))]
        (if icon
          (add-upd-label! skin table update-fn icon)
          (add-upd-label! skin table update-fn))))
    table))

(defn create-dev-menu
  [{:keys [menus update-labels skin]}]
  (let [table (Table.)]
    (doto (.add table ^Actor (dev-menu-main-table skin menus update-labels))
      (.expandX)
      (.fillX)
      (.colspan (int 1)))
    (.row table)
    (doto (.add table ^Actor (doto (Label. "" ^Skin skin)
                               (.setTouchable Touchable/disabled)))
      (.expand)
      (.fillX)
      (.fillY))
    (.row table)
    (doto table
      (.pack)
      (.setFillParent true))))
