package man10.red.mysql_sample;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;

import java.sql.ResultSet;
import java.util.Random;

public class MySQL extends JavaPlugin {
    MySQLManager mysql = null;

    String PluginName = "§e§l[§b§lMySQL§4§lGamble§e§l]";
    public static FileConfiguration config1;

    @Override
    public void onEnable() {
        getCommand("mysql").setExecutor(this);

        saveDefaultConfig();
        FileConfiguration config = getConfig();
        config1 = config;
    }

    ///////////////////////////////
    //command / コマンド
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player)sender;

        // 変数
        int win=0;
        int loss=0;
        int i=0;
        //  大事
        MySQLManager mysql = new MySQLManager(this, "test");

        //String sql ="CREATE TABLE test_3.gamble (UUID varchar(16),win int,loss int);";
        //mysql.execute(sql);

        ///////////////////////////////////////////////
        // UUIDから値 ゲット
        String sql=    "SELECT UUID\n" +
                "FROM  gamble;";
        ResultSet rs = mysql.query(sql);
        try {
            while (rs.next()) {
                if(rs.getString("UUID").equalsIgnoreCase(p.getUniqueId().toString())) {
                    i+=1;
                    ///////////////////////////
                    // win 取得
                    sql=    "SELECT win\n" +
                            "FROM   gamble " +
                            "WHERE UUID='"+p.getUniqueId()+"';";
                    rs = mysql.query(sql);rs.next();
                    win=(int)rs.getInt("win");

                    ///////////////////////////
                    // loss 取得
                    sql=    "SELECT loss\n" +
                            "FROM   gamble " +
                            "WHERE UUID='"+p.getUniqueId()+"';";
                    rs = mysql.query(sql);rs.next();
                    loss=rs.getInt("loss");
                    break;
                }
            }
            rs.close();
        }catch(Exception e){
        }
        ///////////////////////////////////////////////
        // DB なければ作る
        if(i==0){
            p.sendMessage(PluginName+"新しくあなたのMySQLデータを作りました!");
            sql="INSERT INTO gamble (UUID,win,loss) VALUES('"+p.getUniqueId().toString()+"',0,0);";
            mysql.execute(sql);
        }else{
            //  1/2 勝利
            if(new Random().nextInt(2)==0){
                win++;
                p.sendMessage(PluginName+"§c§l勝利! win:"+win);
            }else{//  1/2 敗北
                loss++;
                p.sendMessage(PluginName+"§b§l敗北... loss: "+loss);
            }

            p.sendMessage(PluginName+"§c§lwin:"+win+" §b§lloss:"+loss+" §e§lあなたのKDR: "+(double)win/(double)loss);

            ////////////////
            //  値の変更
            sql="UPDATE gamble SET win="+win+" WHERE UUID='"+p.getUniqueId()+"';";
            mysql.execute(sql);

            sql="UPDATE gamble SET loss="+loss+" WHERE UUID='"+p.getUniqueId()+"';";
            mysql.execute(sql);
        }
        return false;
    }

}

